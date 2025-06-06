[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/YCWZwwXS)

# Evaluation

## OAuth (over Keycloak) with RabbitMQ [Y]

## MiniCube [Y]

## Kubernetes [V]

### Load Balancing [V]

### Resilience [V]
(Which tools are)

### Blue-green deployment [Y]

### Datenbank in Kubernetes [Y]

### Keycloak in Kubernetes vs. Online Keycloak-Provider [V]
#### Hosting Keycloak in Kubernetes
The following approach creates a fresh Keycloak instance in Kubernetes. This does not yet handle a realm-import at creation time.
https://www.keycloak.org/getting-started/getting-started-kube


#### Online Keycloak-Provider
https://www.cloud-iam.com/
The 

### RabbitMQ in Kubernetes [V]
To deploy RabbitMQ in Kubernetes the [**RabbitMQ Operator**](https://www.rabbitmq.com/kubernetes/operator/operator-overview) should be used.
It is important to note RabbittMQ provides two different operators:
- RabbitMQ Cluster Kubernetes Operator
- RabbitMQ Messaging Topology Operator

For the **RabbitMQ Cluster Kubernetes Operator** to work properly [no custom credentials](https://www.rabbitmq.com/kubernetes/operator/operator-overview#top-op-limitations)
should be used in **RabbitMQ Messaging Topology Operator**, since it relies on the default credentials.

In order to successfully run [RabbitMQ](https://www.rabbitmq.com/kubernetes/operator/tls-topology-operator) TLS needs to be used.

## CI/CD-Pipeline (GitHub Actions) [Y]

Continous Integration and Deployment is an essential component to effectively and reliably publish an application, especially to a container platform. By using these practices, a clean deployment can be guaranteed.

![](images/cicd.png)

![](images/feature-integration.png)
