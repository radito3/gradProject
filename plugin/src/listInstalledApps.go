package main

import "fmt"

type listInstalledApps struct {
	cl client
}

func (c *listInstalledApps) execute(args []string) error {
	if len(args) != 1 {
		return fmt.Errorf("Incorrect usage.\nCorrect usage: cf list-installed")
	}

	resp, err := c.cl.manageApmCalls("list_apps", "GET", "installed")
	if err != nil {
		return err
	}

	fmt.Println(fmt.Sprintf("Installed apps:\n%s", resp))
	return nil
}
