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
      dist          = './target/classes/static',
      vendorFiles   = [src + '/js/third-party/tether.js',
                      src + '/js/third-party/bootstrap.js',
                      src + '/js/third-party/selectize.js',
                      src + '/js/app.js'];
//You will need this in webpack-config, too.
var isProduction = argv.production;
global.IsProduction = isProduction;

// Optimize the imgs, cache stuff that already ran through imagemin
gulp.task('images', function() {
  return gulp.src(src + '/img/**/*')
    .pipe(
      imagemin([
       imagemin.gifsicle(),
       imagemin.jpegtran({progressive: true}),
       imagemin.optipng(),
       imagemin.svgo()
      ]))
    .pipe(gulp.dest(dist + '/img'))
});

// Clean the dist folder
gulp.task('clean', function() {
  return del(dist).then(paths => {
    console.log('Deleted '+ dist +' files and folders:\n', paths.join('\n'));
  });
});

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

/*
 Concatenate vendors JS's and app.js.
 Use specific order to avoid missing dependencies.
 Dependencies tasks are called synchronous.
*/
 gulp.task('js-concatenate', function() {
   //gulp.start('js', function() {
   //});

  return gulp.src(vendorFiles)
            .pipe(concat('app.js'))
            .pipe(uglify({preserveComments: 'license'}))
            .pipe(gulp.dest(dist + '/js/'));
});

// Uglify the JS's
gulp.task('js', function () {
  var toReturn = gulp.src([      src + '/js/**/*.js',
                   '!' + src + '/js/third-party/**/*.js']);
  return JSTask(toReturn, dist + '/js/');
});

function JSTask(aGulpSRC, aDestinationFolder) {
  return  aGulpSRC.pipe(babel({
            presets: ['es2015']
          }))
          .pipe(uglify({preserveComments: 'license'}).on('error', function (e) {
              console.log(e);
          }))
          .pipe(gulp.dest(aDestinationFolder));
}

gulp.task('default',   ['css', 'js-concatenate', 'images']);

//gulp.task('default',   ['clean']);