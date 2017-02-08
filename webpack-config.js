var webpack = require("webpack");
var argv    = require('yargs').argv;

var path = "./assets/js";

function GetFiles (aDir, aFiles){
  const fs = require('fs');
  aFiles = aFiles || [];
  var files = fs.readdirSync(aDir);
  for (var i in files){
    var name = aDir + '/' + files[i];
    if (fs.statSync(name).isDirectory()){
      GetFiles(name, aFiles);
    } else {
      aFiles.push(name);
    }
  }
  return aFiles;
}

function ComputeModulesMap (){
  var filesMap = {};


  var allFiles = GetFiles(path);

  for(var i = 0; i < allFiles.length; i++)
  {
    const fs = require('fs');
    var file = allFiles[i];
    if(fs.lstatSync(file).isDirectory() == false)
    {
      var fileName = file.replace(".js", "");
      if(fileName.indexOf("third-party") < 0)
      {
        fileName = fileName.replace(path + "/", "");
        filesMap[fileName] = file;
      }
    }
  }

  return filesMap
}

module.exports = {
  //Use the line below webpack all files:
  //entry: ComputeModulesMap(),
  //watch: IsProduction != 1 ? true : false,
  watch: false,
  entry: {
    'index': path + "/index.js",
    'vee-validate': path + "/vee-validate.js",
    'vue': path + "/vue.js"
  },
  output: {
    filename: '[name].js'
  },
  plugins: [new webpack.optimize.UglifyJsPlugin({
      compress: { warnings: false }
    })]
};
