package main

import "fmt"

type install struct {
	cl client
}

func (c *install) execute(args []string) error {
	if len(args) != 2 {
		return fmt.Errorf("Incorrect usage.\nCorrect usage: cf install <app_name>")
	}

	resp, err := c.cl.manageApmCalls("install", "POST", args[1])
	if err != nil {
		return err
	}

	fmt.Println(resp)
	return nil
}
