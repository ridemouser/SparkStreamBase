Spark streaming program that 
	- reads transaction messages from kafka topic "transaction"
	- looks up Account table from hive
	- Enhances the transaction data from kafka with metadata from hive's account table
	- saves the merged transaction records in a Hive table on HDFS