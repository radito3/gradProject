package main

import "fmt"

type delete struct {
	cl client
}

func (c *delete) execute(args []string) error {
	if len(args) != 2 {
		return fmt.Errorf("Incorrect usage.\nCorrect usage: cf remove <app_name>")
	}

	resp, err := c.cl.manageApmCalls("delete", "DELETE", args[1])
	if err != nil {
		return err
	}

	fmt.Println(resp)
	return nil
}
