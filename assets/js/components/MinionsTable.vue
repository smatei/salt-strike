<template>
  <vuetable ref="vuetable"
    api-url="/minions"
    :fields="['minion', 'nodename', 'os', 'osmajorrelease', 'osarch', 'cpuarch', 'mem_total']"
    class="table table-striped table-hover"
    @vuetable:loading="showLoader"
    @vuetable:load-success="onLoadSuccess"
    @vuetable:loaded="hideLoader"
  ></vuetable>
</template>

<script>
import Vuetable from 'vuetable-2/src/components/Vuetable.vue'

export default {
  components: {
    Vuetable
  },
  methods: {
    showLoader: function() {
      this.$parent._data.loading = 'loading';
    },
    hideLoader: function() {
      this.$parent._data.loading = ''
    },
    onLoadSuccess (response) {
    }
  }
}
</script>
