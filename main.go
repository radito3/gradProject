package main

import (
	"fmt"
	"net/http"
	"code.cloudfoundry.org/cli/plugin"
	"io/ioutil"
	//os
)

type BasicPlugin struct{}

func (c *BasicPlugin) Run(cliConnection plugin.CliConnection, args []string) {

	if args[0] != "apm" {
		fmt.Println("Incorrect usage.\nCorrect usage: cf apm <command>")
		return
	}

	switch {
		case args[1] == "test-app":
			//uri could be from os.Getenv()
			resp, err := http.Get(args[1])
		
			if err != nil {
				fmt.Println("Sevice error on Get: ", err)
				return
			}
		
			defer resp.Body.Close()
		
			bs, readErr := ioutil.ReadAll(resp.Body)
		
			if readErr != nil {
				fmt.Println("Sevice error on Read: ", readErr)
				return
			}
		
			fmt.Println("Service One Response: ", string(bs))

		case args[1] == "list-apps":
			//list available apps from repo

		case args[1] == "install":
			//if 'install' is spelled wrong ->
			fmt.Println("Incorrect usage.\nCorrect usage: cf apm install <app_name>")
			//check if app name is correct
			//->if correct - install
			//->else 
			fmt.Println("Incorrect application name.")

		case args[1] == "update":
			//check for correct spelling
			fmt.Println("Incorrect usage.\nCorrect usage: cf apm update <app_name>")
			//check for correct app name
			fmt.Println("Incorrect application name.")

		case args[1] == "delete":
			//check for correct spelling
			fmt.Println("Incorrect usage.\nCorrect usage: cf apm delete <app_name>")
			//check for correct app name
			fmt.Println("Incorrect application name.")
	}
	
}

func (c *BasicPlugin) GetMetadata() plugin.PluginMetadata {
	return plugin.PluginMetadata {
		Name: "apmPlugin",
		Version: plugin.VersionType {
			Major: 1,
			Minor: 1,
			Build: 2,
		},
		MinCliVersion: plugin.VersionType {
			Major: 6,
			Minor: 7,
			Build: 0,
		},
		Commands: []plugin.Command {
			{
				Name:     "apm",
				HelpText: "application package manager",
				UsageDetails: plugin.Usage{
					Usage: "cf apm <command>",
				},
			},
		},
	}
}

// Main will be used to initialize the plugin process,
// as well as any dependencies you might require for your plugin.
func main() {
	// Any initialization for your plugin can be handled here
	//
	// Note: to run the plugin.Start method, we pass in a pointer to the struct
	// implementing the interface defined at "code.cloudfoundry.org/cli/plugin/plugin.go"
	//
	// Note: The plugin's main() method is invoked at install time to collect
	// metadata. The plugin will exit 0 and the Run([]string) method will not be
	// invoked.
	plugin.Start(new(BasicPlugin))
}
