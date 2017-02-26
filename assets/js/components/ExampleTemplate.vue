<template>
  <div>
    Example <br>
    prop: {{prop}}<br>    
    field: {{field}}<br>
  </div>
</template>

<script>
export default {
  data () {
    return {
      field: this.prop
    }
  },
  props: {
    prop: {
      type: String,
      required: true
    }
  }  
}
</script>