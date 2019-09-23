# Duct RabbitMQ


[Integrant][] methods for connecting to a [RabbitMQ][] server via
[Langohr][].

[integrant]: https://github.com/weavejester/integrant
[rabbitmq]: https://www.rabbitmq.com
[langohr]: http://clojurerabbitmq.info/

## Installation

To install, add the following to your project `:dependencies`

    [viebel/rabbitmq "0.1.2"]

## Usage

This library provides: 
* a multimethod for `:duct.amqp.rabbitmq/langohr` 
that initiates the connection based
on the settings.
* a multimethod for `:duct.amqp.rabbitmq/langohr-consumers` that subscribes handlers on queues.
* a multimethod for `:duct.amqp.rabbitmq/langohr-producers` that creates publish functions for producers.


## Connection 

### URI

```edn
:duct.amqp.rabbitmq/langohr {:host "localhost"
                             :port 5672
                             :username "guest"
                             :password "guest"
                             :vhost "/"}
```

## Consumers

```edn
:duct.amqp.rabbitmq/langohr-consumers {:consumers [{:queue "my-queue"
                                                    :declare-queue {:durable false
                                                                    :exclusive false
                                                                    :auto-delete true
                                                                    :arguments {}}
                                                     :handler #ig/ref :my-prj.handler/handle-msg}]
                                       :connection #ig/ref :duct.amqp.rabbitmq/langohr}
:my-prj.handler/handle-msg {}
```

Note: You can optionally pass a duct logger. If you do so, a message will be logged for each queue subscriber, when it subscribes.

```edn
:duct.amqp.rabbitmq/langohr-consumers {:consumers [{:queue "my-queue"
                                                    :declare-queue {:durable false
                                                                    :exclusive false
                                                                    :auto-delete true
                                                                    :arguments {}}
                                                     :handler #ig/ref :my-prj.handler/handle-msg}]
                                       :connection #ig/ref :duct.amqp.rabbitmq/langohr
                                       :logger #ig/reg :duct/logger}
:my-prj.handler/handle-msg {}
```

```Clojure
(defmethod ig/init-key :my-prj.handler/handle-msg [_ _]
  (fn  [ch {:keys [content-type delivery-tag type] :as meta} ^bytes payload]
    (println (format "[consumer] Received a message: %s, delivery tag: %d, content type: %s, type: %s" (String. payload "UTF-8") delivery-tag content-type type))))
```

Instead of `:connection`, you can have `:connection-settings` like this:

```edn
:duct.amqp.rabbitmq/langohr-consumers {:consumers [{:queue "my-queue"
                                                    :declare-queue {:durable false
                                                                    :exclusive false
                                                                    :auto-delete true
                                                                    :arguments {}}
                                                     :handler #ig/ref :my-prj.handler/handle-msg}]
                                       :connection-settings {:host "localhost"
                                       :port 5672
                                       :username "guest"
                                       :password "guest"
                                       :vhost "/"}
:my-prj.handler/handle-msg {}
```

## Producers

```edn
:duct.amqp.rabbitmq/langohr-producers {:producers [{:name "my-producer"
                                                    :exchange "my-excahnge"
                                                    :routing-key "my-key"}]
                                       :connection #ig/ref :duct.amqp.rabbitmq/langohr}
```

The return value is a map whose keys are the name of the producers (left as strings) and hte values and the values are a map `{:publish-fn fun :ch ch}`, where `fun` is a function that publishes to the corresponding exchange and routing key according to [langohr.basic/publish][] semantics, where the two first parameters are fixed according to the specified exchange and routing key in the settings.

[langohr.basic/publish]: http://reference.clojurerabbitmq.info/langohr.basic.html#var-publish

```clojure
(def publish (get-in system [:duct.amqp.rabbitmq/langohr-producers "my-producer" :publish-fn]))
(publish "Hello!" {:content-type "text/plain" :type "greetings.hi"})
```

Instead of `:connection`, you can have `:connection-settings` like we showed in the `Consumers` section.

## Example

Consider an application that passes messages to itslef through RabbitMQ.

The duct config might look like this:

```edn
{:duct.amqp.rabbitmq/langohr {:host "localhost"
                             :port 5672
                             :username "guest"
                             :password "guest"
                             :vhost "/"}
:duct.amqp.rabbitmq/langohr-consumers {:consumers [{:queue "my-queue"
                                                    :declare-queue {:durable false
                                                                    :exclusive false
                                                                    :auto-delete true
                                                                    :arguments {}}
                                                     :handler #ig/ref :my-prj.handler/handle-msg}]
                                       :connection #ig/ref :duct.amqp.rabbitmq/langohr}
:my-prj.handler/handle-msg {}
:duct.amqp.rabbitmq/langohr-producers {:producers [{:name "my-producer"
                                                    :exchange "my-excahnge"
                                                    :routing-key "my-key"}]
                                       :connection #ig/ref :duct.amqp.rabbitmq/langohr}

}
```

And the producers and consumers might be initialized like this:

```clojure
(defmethod ig/init-key :my-prj.handler/handle-msg [_ _]
  (fn  [ch {:keys [content-type delivery-tag type] :as meta} ^bytes payload]
    (println (format "[consumer] Received a message: %s, delivery tag: %d, content type: %s, type: %s" (String. payload "UTF-8") delivery-tag content-type type))))
    
(def publish (get-in system [:duct.amqp.rabbitmq/langohr-producers "my-producer" :publish-fn]))

(publish "Hello!" {:content-type "text/plain" :type "greetings.hi"})


```

For more information using Langohr, you can start with their
[getting started](http://clojurciemongodb.info/articles/getting_started.html)
webpage.

## License

Copyright © 2019 Yehonathan Sharvit

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
