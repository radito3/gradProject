package main

import "fmt"

type update struct {
	cl client
}

func (c *update) execute(args []string) error {
	if len(args) != 2 {
		return fmt.Errorf("Incorrect usage.\nCorrect usage: cf update <app_name>")
	}

	resp, err := c.cl.manageApmCalls("update", "PUT", args[1])
	if err != nil {
		return err
	}

	fmt.Println(resp)
	return nil
}
