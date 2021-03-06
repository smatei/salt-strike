module.exports = {
  resolve: {
    alias: {
      'vue$': 'vue/dist/vue.js'
    }
  },
  // This is the "main" file which should include all other modules
  entry: {
    'index': './assets/js/index.js',
    'run': './assets/js/run.js',
    'example': './assets/js/example.js'
  },
  // Where should the compiled file go?
  output: {
    // To the `dist` folder
	path: __dirname + '/target/classes/static/js/',
    // With the filename `build.js` so it's dist/build.js
    filename: '[name].js'
  },
  module: {
    // Special compilation rules
    loaders: [
      {
        // Ask webpack to check: If this file ends with .js, then apply some transforms
        test: /\.js$/,
        // Transform it with babel
        //loader: 'babel',
        // don't transform node_modules folder (which don't need to be compiled)
        exclude: /node_modules/
      },
      {
        test: /\.vue$/,
        loader: 'vue-loader'
      },
      { test: /\.css$/, loader: "style-loader!css-loader" }
    ]
  }
};
