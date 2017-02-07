"use strict";

const gulp          = require('gulp'),
      gulpsync      = require('gulp-sync')(gulp),
      sass          = require('gulp-sass'),
      autoprefixer  = require('gulp-autoprefixer'),
      concat        = require('gulp-concat'),
      webpack       = require('webpack-stream'),
      webpack2      = require('webpack'),
      uglify        = require('gulp-uglify'),
      babel         = require('gulp-babel'),
      imagemin      = require('gulp-imagemin'),
      argv          = require('yargs').argv,
      del           = require('del'),
      gulpif        = require('gulp-if'),
      replace       = require('gulp-replace');

let   src           = './assets',
      dist          = './target/classes/static';

//You will need this in webpack-config, too.
var isProduction = argv.production;
global.IsProduction = isProduction;

// Compile the SCSS files, run autoprefixer and compress the resulted CSS's
gulp.task('css', function() {
  var replaceStr = 'url(/assets/';
  if (argv.buildno)
  {
    replaceStr = 'url(/' + argv.buildno + '/assets/';
    console.log('Current buildno is : ' + argv.buildno);
  }

  return gulp.src(src + '/scss/**/*.scss')
    .pipe(replace('url(/assets/', replaceStr))
    .pipe(
      sass({
        includePaths: [src + '/scss'],
        outputStyle: 'compressed',
        errLogToConsole: true
    }))
    .pipe(
      autoprefixer({
        browsers: ['last 2 versions'],
        cascade: false
    }))
    .pipe(gulp.dest(dist + '/css/'))
});

gulp.task('default',   ['css']);