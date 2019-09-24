(ns duct.amqp.rabbitmq.langohr.connection
  (:require [integrant.core :as ig]
            [langohr.core :as rmq]))

(defmethod ig/init-key :duct.amqp.rabbitmq.langohr/connection [_ {:keys [uri] :as settings}]
  (let [settings (if uri
                   (rmq/settings-from uri)
                   settings)]
    (rmq/connect settings)))


(defmethod ig/halt-key! :duct.amqp.rabbitmq.langohr/connection [_ conn]
  (when-not (rmq/closed? conn)
    (rmq/close conn)))

(defmethod ig/suspend-key! :duct.amqp.rabbitmq.langohr/connection [_ _])
