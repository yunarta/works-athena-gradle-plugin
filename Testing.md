# Testing

## Not tested

### incremental build of carthage update

Whenever carthage update is executed, it will first try to refresh the Cartfile.resolved. However as the git source is not updated, the Cartfile.resolved that carthage update will use is not updated. As the result carthage update task is not repeated.

Real test with actual git source update will be quite complex as it requires git with http access rather than SSH.  

