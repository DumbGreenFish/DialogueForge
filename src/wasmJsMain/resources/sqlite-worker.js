// SQLite Web Worker for androidx.sqlite-web 2.7.0-alpha06.
// Adapted from AOSP sqlite-web-worker-test/web-worker/worker.js (Apache-2.0).
// Implements the message protocol expected by WebWorkerSQLiteDriver:
//   { id, data: { cmd: 'open' | 'prepare' | 'step' | 'close', ... } }
// Responds with { id, data: {...} } or { id, error: '...' }.

import sqlite3InitModule from './sqlite/index.mjs';

let sqlite3 = null;

const databases = new Map();
const statements = new Map();

let nextDatabaseId = 0;
let nextStatementId = 0;

function openRequest(id, requestData) {
    try {
        const newDatabaseId = nextDatabaseId++;
        const newDatabase = new sqlite3.oo1.OpfsDb(requestData.fileName);
        databases.set(newDatabaseId, newDatabase);
        postMessage({ id, data: { databaseId: newDatabaseId } });
    } catch (error) {
        postMessage({ id, error: error.message });
    }
}

function prepareRequest(id, requestData) {
    try {
        const newStatementId = nextStatementId++;
        const resultData = { statementId: newStatementId, parameterCount: 0, columnNames: [] };
        const database = databases.get(requestData.databaseId);
        if (!database) {
            postMessage({ id, error: 'Invalid database ID: ' + requestData.databaseId });
            return;
        }
        const statement = database.prepare(requestData.sql);
        statements.set(newStatementId, statement);
        resultData.parameterCount = sqlite3.capi.sqlite3_bind_parameter_count(statement);
        for (let columnIndex = 0; columnIndex < statement.columnCount; columnIndex++) {
            resultData.columnNames.push(sqlite3.capi.sqlite3_column_name(statement, columnIndex));
        }
        postMessage({ id, data: resultData });
    } catch (error) {
        postMessage({ id, error: error.message });
    }
}

function stepRequest(id, requestData) {
    const statement = statements.get(requestData.statementId);
    if (!statement) {
        postMessage({ id, error: 'Invalid statement ID: ' + requestData.statementId });
        return;
    }
    try {
        const resultData = { rows: [], columnTypes: [] };
        statement.reset();
        statement.clearBindings();
        for (let bindingIndex = 0; bindingIndex < requestData.bindings.length; bindingIndex++) {
            statement.bind(bindingIndex + 1, requestData.bindings[bindingIndex]);
        }
        while (statement.step()) {
            if (!resultData.columnTypes.length) {
                for (let columnIndex = 0; columnIndex < statement.columnCount; columnIndex++) {
                    resultData.columnTypes.push(sqlite3.capi.sqlite3_column_type(statement, columnIndex));
                }
            }
            resultData.rows.push(statement.get([]));
        }
        postMessage({ id, data: resultData });
    } catch (error) {
        postMessage({ id, error: error.message });
    }
}

function closeRequest(id, requestData) {
    if (requestData.statementId !== undefined && requestData.statementId !== null) {
        const statement = statements.get(requestData.statementId);
        if (!statement) {
            postMessage({ id, error: 'Invalid statement ID: ' + requestData.statementId });
            return;
        }
        try {
            statement.finalize();
            statements.delete(requestData.statementId);
        } catch (error) {
            postMessage({ id, error: error.message });
        }
    }
    if (requestData.databaseId !== undefined && requestData.databaseId !== null) {
        const database = databases.get(requestData.databaseId);
        if (!database) {
            postMessage({ id, error: 'Invalid database ID: ' + requestData.databaseId });
            return;
        }
        try {
            database.close();
            databases.delete(requestData.databaseId);
        } catch (error) {
            postMessage({ id, error: error.message });
        }
    }
}

const commandMap = {
    'open':    openRequest,
    'prepare': prepareRequest,
    'step':    stepRequest,
    'close':   closeRequest,
};

function handleMessage(event) {
    const requestMessage = event.data;
    if (!Object.hasOwn(requestMessage, 'data') || requestMessage.data == null) {
        postMessage({ id: requestMessage.id, error: "Invalid request, missing 'data'." });
        return;
    }
    if (!Object.hasOwn(requestMessage.data, 'cmd') || requestMessage.data.cmd == null) {
        postMessage({ id: requestMessage.id, error: "Invalid request, missing 'cmd'." });
        return;
    }
    const command = requestMessage.data.cmd;
    const requestHandler = commandMap[command];
    if (requestHandler) {
        requestHandler(requestMessage.id, requestMessage.data);
    } else {
        postMessage({ id: requestMessage.id, error: "Invalid request, unknown command: '" + command + "'." });
    }
}

const messageQueue = [];

onmessage = (event) => {
    if (!sqlite3) {
        messageQueue.push(event);
    } else {
        handleMessage(event);
    }
};

sqlite3InitModule().then(instance => {
    sqlite3 = instance;
    while (messageQueue.length > 0) {
        handleMessage(messageQueue.shift());
    }
});
