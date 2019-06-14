package main

import (
	"encoding/json"
	"strings"
	"code.cloudfoundry.org/cli/plugin"
	"code.cloudfoundry.org/cli/plugin/models"
	"fmt"
)

type client struct {
	org   string
	space string
	token string
	app   plugin_models.GetAppModel
}

type Response struct {
	Error   string   `json:"error"`
	Message string   `json:"result"`
	Apps    []string `json:"apps"`
}

func (c *client) manageApmCalls(apmCall string, httpVerb string, appName string,
	query ...string) (string, error) {

	uri := "https://" + c.app.Routes[0].Host + "." + c.app.Routes[0].Domain.Name + "/" +
		c.org + "/" + c.space + "/" + apmCall + "/" + appName

	resp, err := httpCall(httpVerb, uri, c.token)
	if err != nil {
		return "", err
	}

	var res Response
	if err := json.Unmarshal([]byte(resp), &res); err != nil {
		return "", err
	}

	if len(res.Error) != 0 {
		return "", fmt.Errorf(res.Error)
	} else if len(res.Apps) != 0 {
		return strings.Join(res.Apps, "\n"), nil
	} else {
		return res.Message, nil
	}
}

func getClient(con plugin.CliConnection) (*client, error) {
	app, err := con.GetApp("apmServices")
	if err != nil {
		return nil, err
	}

	org, err := con.GetCurrentOrg()
	if err != nil {
		return nil, err
	}

	space, err := con.GetCurrentSpace()
	if err != nil {
		return nil, err
	}

	token, err := con.AccessToken()
	if err != nil {
		return nil, err
	}

	return &client{org.Name, space.Name, token, app}, nil
}
