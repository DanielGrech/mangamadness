package common

import (
    "code.google.com/p/go.crypto/bcrypt"
)

func HashPassword(input string) (string) {
	bytes, _ := bcrypt.GenerateFromPassword([]byte(input), bcrypt.DefaultCost)
	return string(bytes)
}