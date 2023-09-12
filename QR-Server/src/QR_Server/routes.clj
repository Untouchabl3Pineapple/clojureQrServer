(ns QR-Server.routes
  (:require [QR-Server.handlers :as handlers]
            [compojure.core :refer [defroutes GET POST]]
            [QR-Server.pages :as pages]
            [QR-Server.auth :as cas]
            [QR-Server.db :as db]))

(defroutes app-routes
  (GET "/home" [] handlers/home-handler)
  (GET "/scanner" [] (pages/scanner-page))
  (GET "/logger" [] handlers/logger-handler)
  (GET "/login" [] handlers/login-handler)
  (GET "/logout" [] handlers/logout-handler)
  (POST "/login" [] cas/cas-auth)

  (POST "/log" [qrData] (db/insert-log qrData))

  (GET "/qrgenerator" [] (pages/qr-page))
  (POST "/qrgenerator" [text] (handlers/qr-generator text)))