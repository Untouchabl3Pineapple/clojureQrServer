(ns QR-Server.routes
  (:require [QR-Server.handlers :as handlers]
            [compojure.core :refer [defroutes GET POST]]))

(defroutes app-routes
  (GET "/home" [] (handlers/home-gethandler))

  (GET "/scanner" [] (handlers/scanner-gethandler))
  (GET "/logger" [] (handlers/logger-gethandler))
  (POST "/log" [qrData] (handlers/log-posthandler qrData))

  (GET "/qrgenerator" [] (handlers/qr-generator-gethandler))
  (POST "/qrgenerator" [text] (handlers/qr-generator-posthandler text))

  (GET "/login" [] (handlers/login-gethandler))
  (POST "/login" [] (handlers/login-posthandler))
  (GET "/logout" [] (handlers/logout-gethandler)))