<template>
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
      <button type="button" class="btn btn-primary">
        Run
      </button>
    </div>
  </div>
</template>

<script>
export default {
  data () {
    return {
      moduleField: "Test",
      functionField: "ping"
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
  }
}
</script>
