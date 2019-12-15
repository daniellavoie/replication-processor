-- Create the test database
CREATE DATABASE cdcpoc;
GO
USE cdcpoc;
EXEC sys.sp_cdc_enable_db;
GO