The files in this directory where created as follows:
```
$ openssl genrsa -out reallifedeveloper.com 1024
$ openssl req -new -key test.key -out test.csr
$ openssl x509 -req -days 3650 -in test.csr -signkey test.key -out test.crt
$ openssl pkcs12 -export -nokeys -in test.crt -out test.pfx -name rld # Did not work, the cert is not visible in keytool
$ keytool -import -alias rld -noprompt -file test.crt -keystore test.pfx -storetype PKCS12 # password: reallifedeveloper

```