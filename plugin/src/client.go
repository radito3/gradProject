package main

import (
	"code.cloudfoundry.org/cli/plugin"
	"code.cloudfoundry.org/cli/plugin/models"
)

type client struct {
	org   string
	space string
	token string
	app   plugin_models.GetAppModel
}

func (c *client) manageApmCalls(apmCall string, httpVerb string, appName string,
	query ...string) (string, error) {

	uri := "https://" + c.app.Routes[0].Host + "." + c.app.Routes[0].Domain.Name + "/" +
		c.org + "/" + c.space + "/" + apmCall + "/" + appName

	resp, err := httpCall(httpVerb, uri, c.token)
	if err != nil {
		return "", err
	}

	return resp, nil
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

	c := client{org: org.Name, space: space.Name, token: token, app: app}
	return &c, nil
}