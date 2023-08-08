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

package manifold.sql.schema.simple;

import manifold.ext.rt.api.auto;
import manifold.sql.H2SakilaTest;
import manifold.sql.rt.api.TxScope;
import manifold.sql.rt.api.TxScopeProvider;
import manifold.sql.schema.simple.H2Sakila.*;
import org.junit.Test;

public class ScratchTest_Sakila extends H2SakilaTest
{
  @Test
  public void testSomeInterestingQueries()
  {
    TxScope txScope = TxScopeProvider.newScope( H2Sakila.class );
    Stores s = "[>Stores.sql:H2Sakila<] Select * From store";
    for( Store r : s.run( txScope ) )
    {
      System.out.println( r.display() );
      System.out.println( r.getAddressRef().display() );
      System.out.println( r.getManagerStaffRef().display() );
    }

    /* [>ActorWithMostFilms.sql:H2Sakila<]
      SELECT first_name, last_name, count(*) films
      FROM actor AS a
      JOIN film_actor AS fa USING (actor_id)
      GROUP BY a.actor_id, first_name, last_name
      ORDER BY films DESC
      LIMIT 1;
    */
    for (ActorWithMostFilms.Row row : ActorWithMostFilms.run(txScope)) {
      System.out.println(row.display());
    }

    /* [>CumulativeRevenueAllStores.sql:H2Sakila<]
      SELECT payment_date, amount, sum(amount) OVER (ORDER BY payment_date)
      FROM (
        SELECT CAST(payment_date AS DATE) AS payment_date, SUM(amount) AS amount
        FROM payment
        GROUP BY CAST(payment_date AS DATE)
      ) p
      ORDER BY payment_date;
    */
    for (CumulativeRevenueAllStores.Row row : CumulativeRevenueAllStores.run(txScope)) {
      System.out.println(row.getPaymentDate());
      System.out.println(row.getSumAmount_Over_OrderByPaymentDate());
      System.out.println(row.display());
    }

    auto one = "[>.sql:H2Sakila<] select 1 from dual";
    auto run = one.run(txScope);
    run.forEach( r -> System.out.println(r.display()));


    for( Staff staff: "[>.sql:H2Sakila<] select * from staff".run( txScope ) )
    {
      System.out.println( staff.display() );
    }
  }
}