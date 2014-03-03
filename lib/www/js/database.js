var Database = function(dbName, successCallback, errorCallback) {
	if(dbName != null && dbName.length > 0) {
		if(dbName.toLowerCase().indexOf('.db', dbName.length - 3) == -1) {
			dbName += '.db';
		}
    	DynamicApp.exec(successCallback, errorCallback, "Database", "init", [{dbName: dbName}]);
	}
};

Database.prototype = {
    executeSQL: function(query, args, successCallback, errorCallback) {
    	query = query.trim();
		var isSelectQuery = false;
		
		if(args != null && args.isSelectQuery != null) {
			isSelectQuery = args.isSelectQuery;
		} else if(query.toLowerCase().indexOf('select') == 0) {
			isSelectQuery = true;
		}

        var options = {
            sql: query,
            isSelectQuery: isSelectQuery
        };
        
        DynamicApp.exec(successCallback, errorCallback, "Database", "executeSQL", [options]);
    },
    begin: function(successCallback, errorCallback) {
        this.executeSQL('BEGIN TRANSACTION', {isSelectQuery:false}, successCallback, errorCallback);
    },
    commit: function(successCallback, errorCallback) {
        this.executeSQL('COMMIT', {isSelectQuery:false}, successCallback, errorCallback);
    },
    rollback: function(successCallback, errorCallback) {
        this.executeSQL('ROLLBACK', {isSelectQuery:false}, successCallback, errorCallback);
    }
};

Database.SQLITE_OK           = 0;   // Successful result

// beginning-of-error-codes
Database.SQLITE_ERROR        = 1;   // SQL error or missing database
Database.SQLITE_INTERNAL     = 2;   // Internal logic error in SQLite
Database.SQLITE_PERM         = 3;   // Access permission denied
Database.SQLITE_ABORT        = 4;   // Callback routine requested an abort
Database.SQLITE_BUSY         = 5;   // The database file is locked
Database.SQLITE_LOCKED       = 6;   // A table in the database is locked
Database.SQLITE_NOMEM        = 7;   // A malloc() failed
Database.SQLITE_READONLY     = 8;   // Attempt to write a readonly database
Database.SQLITE_INTERRUPT    = 9;   // Operation terminated by sqlite3_interrupt()
Database.SQLITE_IOERR        = 10;   // Some kind of disk I/O error occurred
Database.SQLITE_CORRUPT      = 11;   // The database disk image is malformed
Database.SQLITE_NOTFOUND     = 12;   // Unknown opcode in sqlite3_file_control()
Database.SQLITE_FULL         = 13;   // Insertion failed because database is full
Database.SQLITE_CANTOPEN     = 14;   // Unable to open the database file
Database.SQLITE_PROTOCOL     = 15;   // Database lock protocol error
Database.SQLITE_EMPTY        = 16;   // Database is empty
Database.SQLITE_SCHEMA       = 17;   // The database schema changed
Database.SQLITE_TOOBIG       = 18;   // String or BLOB exceeds size limit
Database.SQLITE_CONSTRAINT   = 19;   // Abort due to constraint violation
Database.SQLITE_MISMATCH     = 20;   // Data type mismatch
Database.SQLITE_MISUSE       = 21;   // Library used incorrectly
Database.SQLITE_NOLFS        = 22;   // Uses OS features not supported on host
Database.SQLITE_AUTH         = 23;   // Authorization denied
Database.SQLITE_FORMAT       = 24;   // Auxiliary database format error
Database.SQLITE_RANGE        = 25;   // 2nd parameter to sqlite3_bind out of range
Database.SQLITE_NOTADB       = 26;   // File opened that is not a database file
Database.SQLITE_ROW          = 100;  // sqlite3_step() has another row ready
Database.SQLITE_DONE         = 101;  // sqlite3_step() has finished executing
// end-of-error-codes