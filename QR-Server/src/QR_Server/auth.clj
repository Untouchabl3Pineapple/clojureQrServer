(ns QR-Server.auth
  (:require

   [ring.util.response :as response]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   ;[QR-Server.core :as core]
   ))



(defn incorrect-login-page []
  (hiccup.core/html
   [:h2 "Incorrect Login"]
   [:form
    (anti-forgery-field)]))


(defn illegal-token []
  (response/content-type (response/not-found "Illegal token ") "text/plain"))

(defn redirect [redirect-url status]
  {:status status
   :headers {"location" redirect-url}})



(def global-token (atom "string"))
(def global-log-in-out (atom "Login"))
(def global-login-route (atom "/login"))
(def global-login-err (atom 0))

(defn reset-all []
  (reset! global-log-in-out "Login")
  (reset! global-token "null")
  (reset! global-login-route "/login"))

(defn verify-token [token]
  (= token "valid_token")) ; Проверяем, является ли токен действительным
(defn- generate-token []
  (str "valid_token")) ; Генерируем случайный токен (str (rand-int 1000000))
(defn- admin-login-validation [login password]
  (and (= login "admin") (= password "admin")))
(defn- user-login-validation [login password]
  (and (= login "user") (= password "user")))

(defn- login-validation [login password]
  (if (or (admin-login-validation login password)
          (user-login-validation login password))
    (do
      (if (admin-login-validation login password)
        (reset! global-token (generate-token)))

      true)

    false))


(defn cas-auth [request]
  (let [params (get-in request [:params])
        login (get params :login)
        password (get params :password)]
       ;; Проверка логина и пароля и генерация токена (пока упрощенно)
    (if (login-validation login password)
      ;then
      (do
        (reset! global-log-in-out "Logout")
        (reset! global-login-route "/logout")
        (redirect "/home" 302))
      ;else
      ;(response/content-type (response/response "incorrect login or password") "text/html")
      (do
        (reset! global-login-err 1)
        (redirect "/login" 302)))))
