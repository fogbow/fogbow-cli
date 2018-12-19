Fogbow-CLI
==========

The Fogbow CLI is a command line interface for the 
[Fogbow Resource Allocation Service (RAS)](https://github.com/fogbow/resource-allocation-service),
the [Membership Service (MS)](https://github.com/fogbow/membership-service) and the
[Federated Network Service](https://github.com/fogbow/network-federated-service). 
Through the Fogbow CLI, users are able to get information about federation members, create, retrieve
and delete compute instances, private and federated networks, storage volumes, etc.

Installation
==========

## Dependencies

```
apt-get install maven
apt-get install openjdk-8-jdk
```

## Build from source

Download it from our repository and then install it with maven.

```
wget https://github.com/fogbow/fogbow-cli.git
```

Now, install it with Maven:

```
cd fogbow-cli
mvn install -Dmaven.test.skip=true
```

## Usage

You can use the executable located in the `bin` folder to use Fogbow-CLI:

```./bin/fogbow-cli [command] [command options]```

## Commands

Obs.: You will notice that most commands specify a `--url` option. This is the URL where the service you are trying to reach is deployed.

### Token operations (token)

#### Create a new Token

Create a new user token.

Note: to pass the credentials it is necessary the use of dynamic parameters; follow the example with the OpenStack credentials:

```
--create (required)
--url (required)
```

Dynamic parameters (Syntax: -Dkey=value):
```
-Dprojectname= (required): dynamic parameter
-Dpassword= (required): dynamic parameter
-Dusername= (required): dynamic parameter
-Ddomain= (required): dynamic parameter
```


### Member operations (member)

#### List federation members

Get the ids of all federation members.

```
--get-all (required)
--url (required)
```


### Images operations (image)

#### List a particular image

Get detailed information about a single image.

```
--get (required)
--url (required)
--federation-token-value (required)
--member-id (required)
--id (required)
```

#### List all images

Get ids of all images.

```
--get-all (required)
--url (required)
--federation-token-value (required)
--member-id (required)
```


### Attachment operations (attachment)

#### Create attachment

```
--create
--federation-token-value or --federation-token-path (required)
--url

--provider
--volumeId
--computeId
--device
```

#### Delete attachment

```
--delete
--url (required)
--federation-token-value or --federation-token-path (required)
--id
```

#### Get attachment

Get informationabout a specific instance.

```
--get (required)
--url (required)
--federation-token-value or --federation-token-path (required)
--id
```

#### Get all attachments status

Get status for all instances orders associated to a particular user's token.

```
--get-all-status (required)
--url (required)
--federation-token-value or --federation-token-path (required)
```


### Compute operations (compute)

#### Create compute

```
--create
--federation-token-value or --federation-token-path (required)
--url

--provider
--public-key
--image-id
--vcpu
--memory
--disk
--fednet-id
```

#### Delete compute

```
--delete
--url (required)
--federation-token-value or --federation-token-path (required)
--id
```

#### Get compute

```
--get (required)
--url (required)
--federation-token-value or --federation-token-path (required)
--id
```

#### Get all computes

```
--get-all (required)
--url (required)
--federation-token-value (required)
```

#### Get all computes status

```
--get-all-status (required)
--url (required)
--federation-token-value or --federation-token-path (required)
```

#### Get Allocation

```
--get-allocation (required)
--member-id (required)
--url (required)
--federation-token-value or --federation-token-path (required)
```

#### Get Quota

```
--get-quota (required)
--member-id (required)
--url (required)
--federation-token-value or --federation-token-path (required)
```


### Federated Network Operations (federated-network)

#### Create Federated Network

```
--create
--federation-token-value or --federation-token-path
--url

--cidrNotation
--name
--allowed-members
```

#### Delete Federated Network

```
--delete
--url (required)
--federation-token-value or --federation-token-path (required)
--id
```

#### Get Federated Network

```
--get (required)
--url (required)
--federation-token-value or --federation-token-path (required)
--id
```

#### Get All Federated Networks Status

```
--get-all-status (required)
--url (required)
--federation-token-value or --federation-token-path (required)
```


### Network Operations (network)

#### Create network

```
--create
--federation-token-value or --federation-token-path
--url
```

```
--provider
--gateway
--cidr
--allocationMode
```

#### Delete network

```
--delete
--url (required)
--federation-token-value or --federation-token-path (required)
--id
```

#### Get network

```
--get (required)
--url (required)
--federation-token-value or --federation-token-path (required)
--id
```

#### Get all networks status

```
--get-all-status (required)
--url (required)
--federation-token-value or --federation-token-path (required)
```


### Volume operations (volume)

#### Create volume

```
--create
--federation-token-value or --federation-token-path
--url

--provider
--volume-size
--name
```

#### Delete volume

```
--delete
--url (required)
--federation-token-value or --federation-token-path (required)
--id
```

#### Get volume

```
--get (required)
--url (required)
--federation-token-value or --federation-token-path (required)
--id
```

#### Get all volumes status

```
--get-all-status (required)
--url (required)
--federation-token-value or --federation-token-path (required)
```


### Public Ip operations (public-ip)

#### Create Public Ip

```
--create
--url (required)
--federation-token-value or --federation-token-path (required)
```

```
--provider
--compute-id (required)
--name
```

#### Delete Public Ip

```
--delete
--url (required)
--federation-token-value or --federation-token-path (required)
--id (required)
```

#### Get Public Ip

```
--get
--url (required)
--id (required)
--federation-token-value or --federation-token-path (required)
```

#### Get Status of All Public Ips Status

```
--get-all-status
--url (required)
--federation-token-value or --federation-token-path (required)
```


### Security Group Rules (security-group-rules)

#### Create Security Group Rule

```
--create
--direction
--portFrom
--portTo
--cidr
--etherType
--protocol
```

#### Delete Security Group Rule

```
--delete
--id
```