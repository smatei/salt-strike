<template>
  <div id="minions">
    <div class="ui container">
      <minions-table></minions-table>
    </div>
  </div>
</template>

<script>
import MinionsTable from './components/MinionsTable.vue'

export default {
  name: 'minions',
  components: {
    MinionsTable
  }
}
</script>

<style>
#minions {
}
</style>
