package main

import (
	"io/ioutil"
	"net/http"
	"strings"
)

func httpCall(method string, uri string, token string) (string, error) {
	client := &http.Client{}

	req, err := http.NewRequest(method, uri, nil)
	if err != nil {
		return "", err
	}

	req.Header.Set("access-token", strings.Split(token, " ")[1])

	resp, err := client.Do(req)
	if err != nil {
		return "", err
	}

	defer resp.Body.Close()

	bs, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		return "", err
	}

	return string(bs), nil
}
