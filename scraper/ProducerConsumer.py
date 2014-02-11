#!/usr/bin/python
import requests
import logging
import ConfigParser

# Read in our config
config = ConfigParser.RawConfigParser()
config.read('default.cfg')

# Setup logger
logging.basicConfig(level=logging.DEBUG,
					format='%(levelname)-8s [%(asctime)s] %(message)s',
                    datefmt='%H:%M:%S',)
logger = logging.getLogger(__name__)

# Shut up noisy library
requests_log = logging.getLogger("requests")
requests_log.setLevel(logging.WARNING)

# Constants
base_url = "http://mangapanda.com"

def get_beanstalk_server():
	c = config
	section = 'beanstalk'
	return c.get(section, 'host'), c.getint(section, 'port')