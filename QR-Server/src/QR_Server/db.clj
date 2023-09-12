(ns QR-Server.db
  (:require 
            [hiccup.core :refer :all]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            ))
(def db-type
  "postgresql")
(def db-name
  "postgres")
(def db-user
  "postgres")
(def db-password
  "12345")
  (def db-host
    "localhost")
(def db-port
  5432)
(def db-params
  {:dbtype   db-type
   :dbname   db-name
   :user     db-user
   :password db-password
   :host     db-host
   :port     db-port})

(defn get-connection []
  (jdbc/get-datasource db-params))

;(def global-token (atom "string")) мб вернуть


(defn get-all-logs [db]
  (jdbc/execute! db ["SELECT * FROM logger ORDER BY log_date_time DESC"] {:builder-fn rs/as-unqualified-lower-maps}))


(defn insert-log [data]
  (jdbc/execute! (get-connection) [(str "INSERT INTO logger(log_date_time, log_data) VALUES (CURRENT_TIMESTAMP, '" data "')")])
  (hiccup.core/html
   data))