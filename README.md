[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/YCWZwwXS)

# Evaluation

## OAuth (over Keycloak) with RabbitMQ [Y]

## MiniCube [Y]

## Kubernetes (K8s) [V]
With Kubernetes different containers can be hosted and managed individually.
Kubernetes can be run locally or on a server/cloud provider.
Additionally, to the basic hosting service, Kubernetes features a number of 
additional features that can be used to ensure the availability of the services.

https://kubernetes.io/

### Load Balancing [V]
For Load-Balancing to be possible first it is necessary to have the amount of 
resources available to run multiple instance. 
Load-balancing can (but must not) be combined with automatic scaling.
In Kubernetes one can choose between different load-balancing methods, 
that work more or less automatically.

In order for load-balancing to work properly in our case there are a few things to consider:
- Authentication
  - How is the user-session preserved over multiple requests, so that the user does not need to log in again, 
    if they get sent to a different  server.
- Database
  - Would we use one or multiple databases to store the sensor information?
  - How would the databases be synced? -> eventual consistency?
  - If we used only one database, would the load-balancing even have an effect, 
    since the computations themselves are not expensive.

https://www.apptio.com/topics/kubernetes/best-practices/load-balancer/?src=kc-com

### Resilience [V]
In Kubernetes one can (relatively) easily setup different methods to improve the resilience of an application. 
These methods are exemplary:

- Self-healing
  - Restarting containers that crash
  - Depending on how the software works internally this can help or the issue can just reoccur
  - Automatically also attached storage
  - Requires health-checks on the application
    - liveness probe (nodes can be restarted when this fails)
    - readiness probe
    - startup probe
- Horizontal resource scaling
  - The application can be scaled up either automatically or manually
  - Of course requires more available resources to be accessed (over a cloud provider)

https://blog.devgenius.io/building-resilient-applications-in-kubernetes-a-hands-on-guide-9f5fffc14b9b

### Blue-green deployment [Y]

### Datenbank in Kubernetes [Y]

### Keycloak in Kubernetes vs. Online Keycloak-Provider [V]
#### Hosting Keycloak in Kubernetes
The following approach creates a fresh Keycloak instance in Kubernetes. 
This does not yet handle a realm-import at creation time.
https://www.keycloak.org/getting-started/getting-started-kube

- Automatically importing realm might prove tricky, since the Keycloak Operator does not support this out of the box.
  - It would be necessary to either manually import or create the realm from scratch
- Can be tricky to handle the SSL certificates to work with RabbitMQ

#### Online Keycloak-Provider
https://www.cloud-iam.com/

The usage of an online Keyclaok provider would be a good alternative, since
- It requires only a one-time setup of the realm in a secluded application
- No handling of Keycloak in Kubernetes is necessary
- In the free version still no SSL certificate are available
  - Might prove even trickier to install one there, since we probably won't have access to the hosted keycloak instance directly.

### RabbitMQ in Kubernetes [V]
To deploy RabbitMQ in Kubernetes the [**RabbitMQ Operator**](https://www.rabbitmq.com/kubernetes/operator/operator-overview) should be used.
It is important to note RabbitMQ provides two different operators:
- RabbitMQ Cluster Kubernetes Operator
- RabbitMQ Messaging Topology Operator

For the **RabbitMQ Cluster Kubernetes Operator** to work properly [no custom credentials](https://www.rabbitmq.com/kubernetes/operator/operator-overview#top-op-limitations)
should be used in **RabbitMQ Messaging Topology Operator**, since it relies on the default credentials.

In order to successfully run [RabbitMQ](https://www.rabbitmq.com/kubernetes/operator/tls-topology-operator) TLS needs to be setup.
And one needs to pass the CA (Certificate Authority) to the RabbitMQ Operator.

## CI/CD-Pipeline (GitHub Actions) [Y]

Continous Integration and Deployment is an essential component to effectively and reliably publish an application, especially to a container platform. By using these practices, a clean deployment can be guaranteed.

![](images/cicd.png)

![](images/feature-integration.png)

# Conclusio
- Simple Resilience measures will be used
  - Automatic restarting of failing containers
- RabbitMQ will be hosted in Kubernetes
  - If problems arise with RabbitMQ. It can be replaced with Mosquitto.
- Keycloak will be used over the remote provider
  - A certificate will be setup by routing the service over an nginx reverse proxy 