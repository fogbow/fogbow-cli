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

### Quota operations (quota)

#### Get quota

Get the quota of a specific member and cloud.

```
--member-id (required)
--cloud-name (required)
--url (required)
--system-user-token or --system-user-token-path (required)
```

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
--system-user-token or --system-user-token-path (required)
```

### Cloud operations

#### List all clouds of that RAS instance

```
--get-all (required)
--url (required)
--system-user-token or --system-user-token-path (required)
```

#### List clouds of a specific member

```
--url (required)
--get (required)
--member-id (required)
--system-user-token or --system-user-token-path (required)
```


### Images operations (image)

#### List a particular image

Get detailed information about a single image.

```
--get (required)
--url (required)
--system-user-token or --system-user-token-path (required)
--member-id (required)
--cloud-name (required)
--id (required)
```

#### List all images

Get ids of all images.

```
--get-all (required)
--url (required)
--system-user-token or --system-user-token-path (required)
--member-id (required)
--cloud-nname (required)
```


### Attachment operations (attachment)

#### Create attachment

```
--create
--system-user-token or --system-user-token-path (required)
--url
--member-id

--volumeId
--computeId
--device
```

#### Delete attachment

```
--delete
--url (required)
--system-user-token or --system-user-token-path (required)
--id
```

#### Get attachment

Get information about a specific instance.

```
--get (required)
--url (required)
--system-user-token or --system-user-token-path (required)
--id
```

#### Get all attachments status

Get status for all instances orders associated to a particular user's token.

```
--get-all (required)
--url (required)
--system-user-token or --system-user-token-path (required)
```


### Compute operations (compute)

#### Create compute

```
--create
--system-user-token or --system-user-token-path (required)
--url

--provider
--cloud-name
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
--system-user-token or --system-user-token-path (required)
--id
```

#### Get compute

```
--get (required)
--url (required)
--system-user-token or --system-user-token-path (required)
--id
```

#### Get all computes

```
--get-all (required)
--url (required)
--system-user-token (required)
```

#### Get Allocation

```
--get-allocation (required)
--member-id (required)
--cloud-name (required)
--url (required)
--system-user-token or --system-user-token-path (required)
```

### Federated Network Operations (federated-network)

#### Create Federated Network

```
--create
--system-user-token or --system-user-token-path
--url

--cidrNotation
--name
--allowed-members
```

#### Delete Federated Network

```
--delete
--url (required)
--system-user-token or --system-user-token-path (required)
--id
```

#### Get Federated Network

```
--get (required)
--url (required)
--system-user-token or --system-user-token-path (required)
--id
```

#### Get All Federated Networks Status

```
--get-all (required)
--url (required)
--system-user-token or --system-user-token-path (required)
```


### Network Operations (network)

#### Create network

```
--create
--system-user-token or --system-user-token-path
--url
```

```
--provider
--cloud-name
--gateway
--name
--cidr
--allocationMode
```

#### Delete network

```
--delete
--url (required)
--system-user-token or --system-user-token-path (required)
--id
```

#### Get network

```
--get (required)
--url (required)
--system-user-token or --system-user-token-path (required)
--id
```

#### Get all networks status

```
--get-all (required)
--url (required)
--system-user-token or --system-user-token-path (required)
```

#### Get Allocation

```
--get-allocation (required)
--member-id (required)
--cloud-name (required)
--url (required)
--system-user-token or --system-user-token-path (required)
```


### Volume operations (volume)

#### Create volume

```
--create
--system-user-token or --system-user-token-path
--url

--provider
--cloud-name
--volume-size
--name
```

#### Delete volume

```
--delete
--url (required)
--system-user-token or --system-user-token-path (required)
--id
```

#### Get volume

```
--get (required)
--url (required)
--system-user-token or --system-user-token-path (required)
--id
```

#### Get all volumes status

```
--get-all (required)
--url (required)
--system-user-token or --system-user-token-path (required)
```

#### Get Allocation

```
--get-allocation (required)
--member-id (required)
--cloud-name (required)
--url (required)
--system-user-token or --system-user-token-path (required)
```


### Public Ip operations (public-ip)

#### Create Public Ip

```
--create
--url (required)
--system-user-token or --system-user-token-path (required)
```

```
--compute-id (required)
--name
```

#### Delete Public Ip

```
--delete
--url (required)
--system-user-token or --system-user-token-path (required)
--id (required)
```

#### Get Public Ip

```
--get
--url (required)
--id (required)
--system-user-token or --system-user-token-path (required)
```

#### Get Status of All Public Ips Status

```
--get-all
--url (required)
--system-user-token or --system-user-token-path (required)
```

#### Get Allocation

```
--get-allocation (required)
--member-id (required)
--cloud-name (required)
--url (required)
--system-user-token or --system-user-token-path (required)
```


### Security Group Rules (security-rule)

#### Create Security Group Rule

```
--create
--url
--system-user-token or --system-user-token-path (required)
```

```
--public-ip-id or --network-id (required)
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
--url
--system-user-token or --system-user-token-path (required)
--id
```