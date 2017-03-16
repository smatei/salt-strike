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

      <div class="card-block" id="result">
      </div>
    </div>
  </div>
</template>

<script>
import JSONFormatter from 'json-formatter-js';

export default {
  data () {
    return {
      moduleField: "Test",
      functionField: "ping",
      loading: "",
      showResults: false,
      resultsModule: "",
      resultsFunction: ""
    }
  },
  watch: {
    moduleField: function (val) {
      console.log(val);
      this.functionField = this.modulesProp[val].default;
    }
  },
  props: {
    modulesProp: {
      type: Object,
      required: true
    }
  },
  methods: {
    runCommand: function() {
      console.log(this.moduleField + " " + this.functionField);
      this.loading = 'loading';
      var obj = this;

      $.ajax({
        type: "POST",
        url: "run",
        data: {
          module: this.moduleField,
          function: this.functionField
        }
      }).done(function (msg) {
        console.log(msg);

        obj.loading = '';
        obj.showResults = true;
        obj.resultsModule = obj.moduleField;
        obj.resultsFunction = obj.functionField;

        document.getElementById("result").innerHTML = '';

        var config = {
          hoverPreviewEnabled: false,
          hoverPreviewArrayCount: 100,
          hoverPreviewFieldCount: 5,
          theme: '',
          animateOpen: true,
          animateClose: true
        };
        var formatter = new JSONFormatter(JSON.parse(msg), 3, config);

        document.getElementById("result").appendChild(formatter.render());
      });
    }
  }
}
</script>
