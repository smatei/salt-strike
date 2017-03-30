<template>
  <div :class="[{'ui basic segment': true}, loading]">
    <div class="card m-2">
      <div class="card-header">
        <strong>Run command</strong>
        <p class="card-subtitle text-muted">
          Select a module and one of its functions
        </p>
      </div>
      <div class="card-block">

        <label for="moduleSelectID" class="form-control-label">
          Module
        </label>
        <select id="moduleSelectID" class="form-control" v-model="moduleField">
          <option v-for="(value, key, index) in modulesProp" :value="key">
            {{key}}
          </option>
        </select>

        <label for="functionSelectID" class="form-control-label">
          Function
        </label>
        <select id="functionSelectID" class="form-control" v-model="functionField">
          <option v-for="(item, index) in modulesProp[moduleField].functions" :value="item.name">
            {{item.name}}
          </option>
        </select>

        <div v-cloak v-show="showParameters()">
          <label for="parametersSelectID" class="form-control-label">
            Parameters
          </label>
          <div id="parametersSelectID" class="form-control">
            <div v-for="(item, index) of parameterFields" class="mb-1 mt-1">
              <input type="text" :placeholder="item.name" class="form-control" v-model="parameterValues[item.name]['value']">
              </input>
            </div>
          </div>
        </div>

        <label for="targetSelectID" class="form-control-label">
          Target
        </label>
        <select id="targetSelectID" class="form-control" v-model="targetField">
          <option value="All">All</option>
          <option value="Hosts">Hosts</option>
          <option value="Group">Group</option>
        </select>

        <div v-cloak v-show="showHosts()">
          <label for="targetHostsID" class="form-control-label">
            Hosts
          </label>
          <v-select id="targetHostsID" :searchable=true multiple
            :onChange=selectionChanged :options=hostList
            :loading=loadingHosts :disabled=loadingHosts></v-select>
        </div>

        <div v-cloak v-show="showGroup()">
          <label for="targetGroupID" class="form-control-label">
            Group
          </label>
          <input id="targetGroupID" type="text"
            placeholder="Group name" v-model="group" class="form-control">
          </input>
        </div>
      </div>
      <div class="card-footer text-muted">
        <button type="button" class="btn btn-primary" v-on:click="runCommand()">
          Run
        </button>
      </div>
    </div>

    <div class="card m-2" v-cloak v-show="showResults">
      <div class="card-header">
        <strong>Command results</strong>
        <p class="card-subtitle text-muted">
          Command results for {{resultsModule}}.{{resultsFunction}}
        </p>
      </div>

      <div class="card-block">
        <div id="result_json_formatted" class="m-4">
        </div>

        <div id="result_json" class="m-4">
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import JSONFormatter from 'json-formatter-js';
import Vue from 'vue';
import vSelect from 'vue-select';
Vue.component('v-select', vSelect);

export default {
  data () {
    return {
      moduleField: "Test",
      functionField: "ping",
      parameterFields: null,
      parameterValues: null,
      loading: "",
      showResults: false,
      resultsModule: "",
      resultsFunction: "",
      targetField: "All",
      selectedHosts: null,
      hostList: [],
      loadingHosts: false,
      group: ""
    }
  },
  watch: {
    moduleField: function (val) {
      console.log(val);
      this.functionField = this.modulesProp[val].default;
    },
    functionField: function (val) {
      var module = this.moduleField;
      var funct = val;

      this.displayParams(module, funct);
    },
    targetField: function (val) {
      if (val == 'Hosts' && this.hostList.length == 0)
      {
        var vueObj = this;
        vueObj.loadingHosts = true;
        $.ajax({
          type: "POST",
          url: "minionlist"
        }).done(function (msg) {
          console.log(msg);
          var result = JSON.parse(msg);

          for (var key in result.data)
          {
            var value = result.data[key]['String/id'];
            vueObj.hostList.push(value);
          }
          vueObj.loadingHosts = false;
        });
      }
    }
  },
  props: {
    modulesProp: {
      type: Object,
      required: true
    }
  },
  methods: {
    displayParams: function(module, funct) {
      var functionsArray = this.modulesProp[module]["functions"];
      for (var key in this.modulesProp[module]["functions"])
      {
        if (functionsArray[key]["name"] == funct)
        {
          if (typeof(functionsArray[key]["parameters"]) === 'undefined')
          {
            this.parameterFields = null;
            this.parameterValues = null;
          }
          else
          {
            this.parameterFields = functionsArray[key]["parameters"];

            this.parameterValues = {};
            this.parameterTypes = {};
            var index = 0;
            for (var pkey in this.parameterFields)
            {
              this.parameterValues[this.parameterFields[pkey]["name"]] = {};
              this.parameterValues[this.parameterFields[pkey]["name"]]["value"] = "";
              this.parameterValues[this.parameterFields[pkey]["name"]]["type"] = this.parameterFields[pkey]["type"];
              this.parameterValues[this.parameterFields[pkey]["name"]]["index"] = index;
              index++;
            }
          }
        }
      }
    },
    selectionChanged: function(value) {
      console.log("selection changed " + value);
      this.selectedHosts = JSON.stringify(value);
    },
    showHosts: function() {
      return this.targetField == "Hosts";
    },
    showParameters: function() {
      return this.parameterFields != null;
    },
    showGroup: function() {
      return this.targetField == "Group";
    },
    runCommand: function() {
      console.log("run " + this.moduleField + " " + this.functionField + " " + JSON.stringify(this.parameterValues));
      this.loading = 'loading';
      var obj = this;

      var target = null;
      switch (this.targetField) {
        case "Group":
          target = this.group;
          break;
        case "Hosts":
          target = this.selectedHosts;
          break;
        default:
          target = "*";
      }

      var dataArray = {
          module: this.moduleField,
          function: this.functionField,
          target_type: this.targetField,
          targets: target
        };

      if (this.parameterValues != null)
      {
        dataArray["params"] = JSON.stringify(this.parameterValues);
      }

      $.ajax({
        type: "POST",
        url: "run",
        data: dataArray
      }).done(function (msg) {
        console.log(msg);

        obj.loading = '';
        obj.showResults = true;
        obj.resultsModule = obj.moduleField;
        obj.resultsFunction = obj.functionField;

        document.getElementById("result_json_formatted").innerHTML = '';
        document.getElementById("result_json").innerHTML = msg;

        var config = {
          hoverPreviewEnabled: true,
          hoverPreviewArrayCount: 100,
          hoverPreviewFieldCount: 5,
          theme: '',
          animateOpen: true,
          animateClose: true
        };
        var formatter = new JSONFormatter(JSON.parse(msg), 3, config);

        document.getElementById("result_json_formatted").appendChild(formatter.render());
      });
    }
  }
}
</script>
