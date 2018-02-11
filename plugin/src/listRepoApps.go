package main

import "fmt"

type listRepoApps struct {
	cl client
}

func (c *listRepoApps) execute(args []string) error {
	if len(args) != 1 {
		return fmt.Errorf("Incorrect usage.\nCorrect usage: cf list-repo")
	}

	resp, err := c.cl.manageApmCalls("list_apps", "GET", "repo")
	if err != nil {
		return err
	}

	fmt.Println(fmt.Sprintf("Repository apps:\n%s", resp))
	return nil
}
