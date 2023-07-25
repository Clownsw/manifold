/*
 * Copyright (c) 2023 - Manifold Systems LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package manifold.sql.rt.connection;

import manifold.rt.api.util.ManClassUtil;
import manifold.sql.rt.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 */
class BasicTxScope implements OperableTxScope
{
  private final DbConfig _dbConfig;
  private final Set<TableRow> _rows;
  private final ReentrantReadWriteLock _lock;

  public BasicTxScope( Class<? extends SchemaType> schemaClass )
  {
    _dbConfig = DbConfigFinder.instance().findConfig( ManClassUtil.getShortClassName( schemaClass ), schemaClass );
    _rows = new LinkedHashSet<>();
    _lock = new ReentrantReadWriteLock();
  }

  @Override
  public DbConfig getDbConfig()
  {
    return _dbConfig;
  }

  @Override
  public Set<TableRow> getRows()
  {
    _lock.readLock().lock();
    try
    {
      return new HashSet<>( _rows );
    }
    finally
    {
      _lock.readLock().unlock();
    }
  }

  @Override
  public void addRow( TableRow item )
  {
    if( item == null )
    {
      throw new IllegalArgumentException( "Item is null" );
    }

    _lock.writeLock().lock();
    try
    {
      _rows.add( item );
    }
    finally
    {
      _lock.writeLock().unlock();
    }
  }

  @Override
  public void removeRow( TableRow item )
  {
    _lock.writeLock().lock();
    try
    {
      _rows.remove( item );
    }
    finally
    {
      _lock.writeLock().unlock();
    }
  }

  @Override
  public boolean containsRow( TableRow item )
  {
    _lock.readLock().lock();
    try
    {
      return _rows.contains( item );
    }
    finally
    {
      _lock.readLock().unlock();
    }
  }

  @Override
  public boolean commit() throws SQLException
  {
    _lock.writeLock().lock();
    try
    {
      if( _rows.isEmpty() )
      {
        return false;
      }

      TableRow first = _rows.stream().findFirst().get();

      ConnectionProvider cp = ConnectionProvider.findFirst();
      try( Connection c = cp.getConnection( getDbConfig().getName(), first.getClass() ) )
      {
        try
        {
          for( ConnectionNotifier p : ConnectionNotifier.PROVIDERS.get() )
          {
            p.init( c );
          }

          c.setAutoCommit( false );

          Set<TableRow> visited = new HashSet<>();
          for( TableRow row : _rows )
          {
            doCrud( c, row, new LinkedHashMap<>(), visited );
          }

          c.commit();

          for( TableRow row : _rows )
          {
            row.getBindings().commit();
          }

          _rows.clear();

          return true;
        }
        catch( SQLException e )
        {
          c.rollback();

          for( TableRow row : _rows )
          {
            row.getBindings().dropHeldValues();
          }

          throw e;
        }
      }
    }
    finally
    {
      _lock.writeLock().unlock();
    }
  }

  private void doCrud( Connection c, TableRow row, Map<TableRow, Set<FkDep>> unresolvedDeps, Set<TableRow> visited ) throws SQLException
  {
    if( visited.contains( row ) )
    {
      return;
    }
    visited.add( row );

    doFkDependenciesFirst( c, row, unresolvedDeps, visited );

    CrudProvider crud = CrudProvider.instance();

    TableInfo ti = row.tableInfo();
    UpdateContext<TableRow> ctx = new UpdateContext<>( this, row, ti.getDdlTableName(), _dbConfig.getName(),
      ti.getPkCols(), ti.getUkCols(), ti.getAllColsWithJdbcType() );

    if( row.getBindings().isForInsert() )
    {
      crud.create( c, ctx );
    }
    else if( row.getBindings().isForUpdate() )
    {
      crud.update( c, ctx );
    }
    else if( row.getBindings().isForDelete() )
    {
      crud.delete( c, ctx );
    }
    else
    {
      throw new SQLException( "Unexpected bindings kind, neither of insert/update/delete" );
    }
    patchUnresolvedDeps( unresolvedDeps.get( row ) );
  }

  private void patchUnresolvedDeps( Set<FkDep> unresolvedDeps ) throws SQLException
  {
    for( FkDep dep : unresolvedDeps )
    {
      Object pkId = dep.pkRow.getBindings().getHeldValue( dep.pkName );
      if( pkId == null )
      {
        throw new SQLException( "pk value is null" );
      }
      dep.fkRow.getBindings().holdValue( dep.fkName, pkId );
    }
  }

  private void doFkDependenciesFirst( Connection c, TableRow row, Map<TableRow, Set<FkDep>> unresolvedDeps, Set<TableRow> visited ) throws SQLException
  {
    for( Map.Entry<String, Object> entry : row.getBindings().entrySet() )
    {
      Object value = entry.getValue();
      if( value instanceof TableRow )
      {
        TableRow pkTableRow = (TableRow)value;
        FkDep fkDep = new FkDep( row, entry.getKey(), pkTableRow, pkTableRow.tableInfo().getPkCols().iterator().next() );

        doCrud( c, pkTableRow, unresolvedDeps, visited );

        // patch fk
        Object pkId = pkTableRow.getBindings().getHeldValue( fkDep.pkName );
        if( pkId != null )
        {
          row.getBindings().holdValue( fkDep.fkName, pkId );
        }
        else
        {
          unresolvedDeps.computeIfAbsent( pkTableRow, __ -> new LinkedHashSet<>() )
            .add( fkDep );
        }
      }
    }
  }

  private static class FkDep
  {
    final TableRow fkRow;
    final String fkName;
    final TableRow pkRow;
    final String pkName;

    public FkDep( TableRow fkRow, String fkName, TableRow pkRow, String pkName )
    {
      this.fkRow = fkRow;
      this.fkName = fkName;
      this.pkRow = pkRow;
      this.pkName = pkName;
    }
  }
}