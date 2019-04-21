# MotionMate

[![Build Status](https://travis-ci.com/tiefensuche/motionmate.svg?branch=master)](https://travis-ci.com/tiefensuche/motionmate)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/3468be87fdba4468a1cb0a63b5fe5b41)](https://www.codacy.com/app/tiefensuche/motionmate?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=tiefensuche/motionmate&amp;utm_campaign=Badge_Grade)
[![Maintainability](https://api.codeclimate.com/v1/badges/de0c4faf9700c6d6bbd9/maintainability)](https://codeclimate.com/github/tiefensuche/motionmate/maintainability)
[![GitHub release](https://img.shields.io/github/release/tiefensuche/motionmate.svg)](https://github.com/tiefensuche/motionmate/releases)
[![GitHub](https://img.shields.io/github/license/tiefensuche/motionmate.svg)](LICENSE)

MotionMate is a very simple and lightweight pedometer / step counting app for Android that counts your daily steps that can be viewed by selecting a day from the calendar view and presents the values in a nice weekly bar chart as well as some other statistics.

It uses the built-in step counter sensor, if it is present on the device and uses a fallback implementation based on the accelerometer sensor otherwise.

It requires no internet access and no special permissions.

<img src="images/motionmate-screenshot-1.png" width="300"/> <img src="images/motionmate-screenshot-2.png" width="300"/>

## License
The project is licensed under GPLv3.

## Dependencies
-   [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) - Apache License 2.0
