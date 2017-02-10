<template>
  <vuetable ref="vuetable"
    api-url="/minions"
    :fields="['minion', 'nodename', 'os', 'osmajorrelease', 'osarch', 'cpuarch', 'mem_total']"
    class="table table-bordered table-striped table-hover"
  ></vuetable>
</template>

<script>
import Vuetable from 'vuetable-2/src/components/Vuetable.vue'

export default {
  components: {
    Vuetable
  }
}
</script>
