import base64
import struct
import requests
from cryptography.hazmat.primitives.asymmetric.rsa import RSAPublicNumbers
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives import serialization

JWKS_URI = 'https://custom-domain.okta.com/oauth2/default/v1/keys'

def intarr2long(arr):
    return int(''.join(["%02x" % byte for byte in arr]), 16)

def base64_to_long(data):
    if isinstance(data, str):
        data = data.encode("ascii")

    _d = base64.urlsafe_b64decode(bytes(data) + b'==')
    return intarr2long(struct.unpack('%sB' % len(_d), _d))

try: 
    session = requests.session()
    pk_components = session.get(JWKS_URI).json()
    
    # Generate the first key
    e = pk_components['keys'][0]['e']
    n = pk_components['keys'][0]['n']
    
    exponent = base64_to_long(e) 
    modulus = base64_to_long(n)
    
    numbers = RSAPublicNumbers(exponent, modulus)
    public_key = numbers.public_key(backend=default_backend())    
    pem = public_key.public_bytes(encoding=serialization.Encoding.PEM, format=serialization.PublicFormat.SubjectPublicKeyInfo)
    vs_key = pem.decode('utf8').replace('-----BEGIN PUBLIC KEY-----', '').replace('-----END PUBLIC KEY-----', '').strip()
    print(vs_key)    
    
except Exception as exc:
    print(exc)
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    


