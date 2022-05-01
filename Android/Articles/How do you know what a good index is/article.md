# How do you know what a good index is?



Indexing is the process of adding indexes which are used to *quickly locate data* without having to search every row in a database table every time the database table is queried or accessed.



Adding an index usually speeds up your SELECT queries but will slow down other queries like INSERT or UPDATE. You should be careful when adding indices to ensure that this additional cost is worth the gain.

If an indexed field is embedded into another Entity via Embedded, it is NOT added as an index to the containing Entity. If you want to keep it indexed, you must re-declare it in the containing Entity.
Similarly, if an Entity extends another class, indices from the super classes are NOT inherited. You must re-declare them in the child Entity or set Entity.inheritSuperIndices() to true.



Indexes can play an important role in query optimization and searching the results speedily from tables. So it is most important step to select which columns to be indexed. There are two major places where we can consider indexing: columns referenced in the WHERE clause and columns used in JOIN clauses. In short, such columns should be indexed against which you are required to search particular records. Suppose, we have a table named buyers where the SELECT query uses indexes like below:

```sql
SELECT
 buyer_id /* no need to index */
FROM buyers
WHERE first_name='Tariq' /* consider to use index */
AND last_name='Iqbal'   /* consider to use index */
```

Since "buyer_id" is referenced in the SELECT portion, MySQL will not use it to limit the chosen rows. Hence, there is no great need to index it. The below is another example little different from the above one:

```sql
SELECT
 buyers.buyer_id, /* no need to index */
 country.name    /* no need to index */
FROM buyers LEFT JOIN country
ON buyers.country_id=country.country_id /* consider to use index */
WHERE
 first_name='Tariq' /* consider to use index */
AND
 last_name='Iqbal' /* consider to use index */
```

According to the above queries first_name, last_name columns can be indexed as they are located in the WHERE clause. Also an additional field, country_id from country table, can be considered for indexing because it is in a JOIN clause. So indexing can be considered on every field in the WHERE clause or a JOIN clause.

The following list also offers a few tips that you should always keep in mind when intend to create indexes into your tables:

- Only index those columns that are required in WHERE and ORDER BY clauses. Indexing columns in abundance will result in some disadvantages.
- Try to take benefit of "index prefix" or "multi-columns index" feature of MySQL. If you create an index such as INDEX(first_name, last_name), don’t create INDEX(first_name). However, "index prefix" or "multi-columns index" is not recommended in all search cases.
- Use the NOT NULL attribute for those columns in which you consider the indexing, so that NULL values will never be stored.
- Use the --log-long-format option to log queries that aren’t using indexes. In this way, you can examine this log file and adjust your queries accordingly.
- 
