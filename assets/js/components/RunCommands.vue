<template>
  <div :class="[{'ui basic segment': true}, loading]">
    <div class="card m-4">
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
          <option v-for="(value, key, index) in modulesProp" :value="key">{{key}}</option>
        </select>

        <label for="functionSelectID" class="form-control-label">
          Function
        </label>
        <select id="functionSelectID" class="form-control" v-model="functionField">
          <option v-for="funct in modulesProp[moduleField].functions" :value="funct.name">{{funct.name}}</option>
        </select>

        <label for="targetSelectID" class="form-control-label">
          Target
        </label>
        <select id="targetSelectID" class="form-control" v-model="targetField">
          <option value="All">All</option>
          <option value="Hosts">Hosts</option>
        </select>

        <div v-cloak v-show="showHosts()">
          <label for="targetHostsID" class="form-control-label">
            Hosts
          </label>
          <v-select :searchable=true multiple :onChange=selectionChanged :options=hostList 
            :loading=loadingHosts :disabled=loadingHosts></v-select>
        </div>
      </div>
      <div class="card-footer text-muted">
        <button type="button" class="btn btn-primary" v-on:click="runCommand()">
          Run
        </button>
      </div>
    </div>

    <div class="card m-4" v-cloak v-show="showResults">
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
      loading: "",
      showResults: false,
      resultsModule: "",
      resultsFunction: "",
      targetField: "All",
      selectedHosts: null,
      hostList: [],
      loadingHosts: false
    }
  },
  watch: {
    moduleField: function (val) {
      console.log(val);
      this.functionField = this.modulesProp[val].default;
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
    selectionChanged: function(value) {
      console.log("selection changed " + value);
      this.selectedHosts = JSON.stringify(value);
    },
    showHosts: function() {
      return this.targetField == "Hosts";
    },
    runCommand: function() {
      console.log(this.moduleField + " " + this.functionField);
      this.loading = 'loading';
      var obj = this;
      var target = (this.targetField == 'All'? '*': this.selectedHosts);
      console.log("target " + target);

      $.ajax({
        type: "POST",
        url: "run",
        data: {
          module: this.moduleField,
          function: this.functionField,
          target_type: this.targetField,
          targets: target
        }
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
