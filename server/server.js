"use strict";

/* jshint node: true */

const express = require('express');
const app = express();
//const session = require('express-session');
//app.use(session({secret: 'secretKey', resave: false, saveUninitialized: false}));

const bodyParser = require('body-parser');
app.use(bodyParser.json());

const async = require('async');

const multer = require('multer');
//const processFormBody = multer({storage: multer.memoryStorage()}).single('uploadedphoto');

app.use(express.static(__dirname));

/*
 * This server exports the following URLs:
 * /                    - Returns a test status message. Good for testing web
 *                        server is running.
 * 
 *
 */

app.get('/', (request, response) => {
  response.send('Simple web server of files from ' + __dirname);
});

const server = app.listen(3000, function() {
  const port = server.address().port;
  console.log('Listening at http://localhost:'
              + port
              + ' exporting the directory '
              + __dirname);
});
