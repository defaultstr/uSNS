package com.usns;

import com.mongodb.*;

public class DB {
  private Mongo mongo = null;
  private DB db = null;

  public static DBCollection getCollection(String name) {
    if (mongo == null) {
      mongo = new Mongo();
      db = mongo.getDB("usns");
    }
    return db.getCollection(name);
  }
}

