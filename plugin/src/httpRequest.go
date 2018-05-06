package main

import (
	"io/ioutil"
	"net/http"
	"strings"
)

func httpCall(method string, uri string, token string) (string, error) {
	client := &http.Client{}
	var repl = strings.Replace
	var split = strings.Split
	const template = "{\"token\":\"<tkn>\",\"user\":\"<user>\",\"pass\":\"<pass>\"}"

	body := strings.NewReader(repl(template, "<tkn>", split(token, " ")[1], 1))

	req, err := http.NewRequest(method, uri, body)
	if err != nil {
		return "", err
	}

	req.Header.Set("auth-type", "token")

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
