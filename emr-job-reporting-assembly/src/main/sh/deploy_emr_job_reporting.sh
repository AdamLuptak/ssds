#!/usr/bin/env bash

# This script server for deploying emr job reporting.

action=$1
template=$2
parameters_file_path=$3
s3_deploy_path=$4
version=1.0-SNAPSHOT

java -cp "emr-job-reporting-assembly-${version}.jar" com.here.owc.EmrJobReportingDeploy "$action" "$template" "$parameters_file_path" "$s3_deploy_path"  || exit 1
