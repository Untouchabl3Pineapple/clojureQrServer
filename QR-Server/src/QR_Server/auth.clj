(ns QR-Server.auth
  (:require
   [ring.util.response :as response]
   [ring.util.anti-forgery :refer [anti-forgery-field]]))


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
  (or
   (= token "user_valid_token")
   (= token "admin_valid_token"))) ; Проверяем, является ли токен действительным
(defn verify-admin-token [token]
  (= token "admin_valid_token"))
(defn- generate-token [login]
  (str login "_" "valid_token")) ; Генерируем случайный токен (str (rand-int 1000000))
(defn- admin-login-validation [login password]
  (and (= login "admin") (= password "admin")))
(defn- user-login-validation [login password]
  (and (= login "user") (= password "user")))

(defn- login-validation [login password]
  (if (or (admin-login-validation login password)
          (user-login-validation login password))
    true
    false))


(defn- authentication [login password]
  (if (login-validation login password)
    (generate-token login)
    (generate-token "invalid")))


(defn cas-auth [request]
  (let [params (get-in request [:params])
        login (get params :login)
        password (get params :password)
        token (authentication login password)]
       ;; Проверка логина и пароля и генерация токена (пока упрощенно)
    (if (verify-token token)
      ;then
      (do
        (reset! global-token token)
        (reset! global-log-in-out "Logout")
        (reset! global-login-route "/logout")
        (redirect "/home" 302))
      ;else
      ;(response/content-type (response/response "incorrect login or password") "text/html")
      (do
        (reset! global-login-err 1)
        (redirect "/login" 302)))))