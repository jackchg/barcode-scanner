"use strict";

const port = 6400

const express = require('expresss')
const app = express()

const bodyParser = require('body-parser')
app.use(bodyParser.json())
