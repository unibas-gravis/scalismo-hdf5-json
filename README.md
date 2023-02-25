# scalismo-hdf-json

Scalismo-hdf-json is a simple library for creating 
and reading files in the [hdf-json format](https://hdf5-json.readthedocs.io/en/latest/).

It currently supports only a subset of all the 
available datatypes, and it is mainly a support
library for the [scalismo](https://github.com/unibas-gravis/scalismo) project.
It is, however, easy to extend it to support more datatypes.

### Why a hdf-json library?

The main reason for creating this library was that it is difficult to 
read and write hdf5 files using pure java. The [jhdf](https://github.com/jamesmudd/jhdf)
project can be used to read hdf5 files. The [nujan](https://github.com/NCAR/nujan) project
can be used to write hdf files. However, both projects do not support the full hdf5 standard, and at the
time of writing are even seem incompatible with each other (files written in nujan cannot be read by jhdf).
Due to the difficulty of writing a full hdf5 library, we decided to use the hdf-json format, which is 
a lot easier to implement. The tools provided by the [hdfgroup](https://github.com/HDFGroup) with the
[hdf-json](https://github.com/HDFGroup/hdf5-json) project make it 
easy to convert hdf5 files to hdf-json files, and vice versa. 

### Status of the project

The project is in an early stage of development and it is currently not clear if 
and how the project will be maintained in the future.

### Copyright and license

All code is available to you under the Apache license, version 2, available at http://www.apache.org/licenses/LICENSE-2.0.

Copyright (c) 2017, University of Basel, Switzerland
