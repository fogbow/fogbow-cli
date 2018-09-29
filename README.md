FOGBOW-CLI
==========

The fogbow CLI is a command line interface for the [Fogbow Resource Allocation Service (RAS)](https://github.com/fogbow/resource-allocation-service). 
Through the fogbow CLI, users are able to get information about federation members; create, retrive and delete instance, network, storage and attachment; and create orders.


Installation
==========

### Depdendencies

`apt-get install maven`   
`apt-get install openjdk-8-jdk`

### Install from source

Download it from our repository and then install it with maven.   

`wget https://github.com/fogbow/fogbow-cli.git`

Now, install it with Maven:

`cd fogbow-clid`   
`mvn install -Dmaven.test.skip=true`   

# Usage

## Token operations (token)

### Create a new Token

Create a new user token.

Note: to pass the credentials and the identity plugin endpoint it is necessary the use of dynamic parameters; follow the example with the OpenStack credentials:

--create (required)
--url (required) : 
-Dpassword= (optional): dynamic parameter
-Dusername= (optional): dynamic parameter
-DAuthUrl= (optional): dynamic parameter


## Member operations (member)

### List federation members

Get the ids of all federation members.

--get-all (required)
--url (required)


## Images operations

### List all images

Get ids of all images.

--get-all (required)
--url (required)
--federation-token-value (required)
--member-id (required)

### Get image

Get detailed information about a single image.

--get (required)
--url (required)
--federation-token-value (required)
--member-id (required)
-- id (required)


## Attachment operations (attachment)

### Create attachment

--create
--federation-token-value or --federation-token-path
--url

--providing-member
--source
--target
--device

### Delete attachment

--federation-token-value or --federation-token-path
--id

### Get all attachments

Get all instance orders associated to a particular user's token.

--get-all (required)
--url (required)
--federation-token-value (required)

### Get attachment

Get all instance orders associated to a particular user's token.

--get-all (required)
--url (required)
--federation-token-value (required)


## Compute operations (compute)

### Create compute

--create
--federation-token-value or --federation-token-path
--url

--providing-member
--public-key
--image-id
--vcpu
--memory
--disk
--fednet-id


## Fednet operations (federated-network)

### Create fednet

--create
--federation-token-value or --federation-token-path
--url

--cidr
--name
--allowed-member


## Network operations (network)

### Create network

--create
--federation-token-value or --federation-token-path
--url

--providing-member
--gateway
--address
--allocation


## Volume operations (volume)

### Create volume

--create
--federation-token-value or --federation-token-path
--url

--providing-member
--volume-size
--name