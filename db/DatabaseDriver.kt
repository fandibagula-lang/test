package org.babetech.borastock.data.db

import app.cash.sqldelight.db.SqlDriver

expect fun provideDriver(): SqlDriver
