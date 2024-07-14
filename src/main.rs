/*
    gass init file
*/
#![allow(non_snake_case)]
#![allow(non_upper_case_globals)]
#![allow(non_camel_case_types)]

#[macro_use]
extern crate lazy_static;

use std::fs::File;
use std::io::{self, Read};
use std::env;
use std::time::{Instant};

mod logger;
mod tokenizer;
mod parser;

pub static _version: &str = "0.1.0";

pub static mut _filePath: String = String::new();

pub static mut _argc: usize       = 0;
pub static mut _argv: Vec<String> = Vec::new();

pub static mut _debugMode: bool = false;

pub static mut _exitCode: bool = false;

fn main() -> io::Result<()> {
    let startTime: Instant = Instant::now();

    use crate::logger::*;
    use crate::tokenizer::*;
    use crate::parser::*;

    // get args -> key-values
    let mut argsKeys: Vec<(String, Vec<String>)> = Vec::new();
    {
        let args: Vec<String> = env::args().collect();
        let mut keyValues: Vec<String> = Vec::new();
        let mut readKey: String = String::new();
        for arg in args.iter().skip(1) {
            //if (arg.len() >= 2 && &arg[0..2] == "--") ||
            if arg.len() >= 1 && &arg[0..1] == "-" {
                // --
                if !readKey.is_empty() {
                    argsKeys.push((readKey.clone(), keyValues.clone()));
                    keyValues.clear();
                }
                readKey = arg.clone();
            } else {
                // read key
                if !readKey.is_empty() {
                    keyValues.push(arg.clone());
                }
            }
        }
        if !readKey.is_empty() {
            argsKeys.push((readKey.clone(), keyValues.clone()));
            keyValues.clear();
        }
    }

    // debug mode on ?
    for (key, values) in &argsKeys {
        // version
        if key == "-v" {
            // todo: version save file ?
            println!("gass v{}",_version);
            std::process::exit(0);
        }
        // debug mode
        if key == "-d" {
            // todo: debug sectors
            // e: ast, structs, interpritation
            unsafe{_debugMode = true;}
        }
    }
    if unsafe{_debugMode} {
        logSeparator("=> Reading arguments");
        log("ok","Debug mode");
    }

    // read args
    let mut noRun:   bool = true;
    let mut runFile: bool = false;
    let mut buffer: Vec<u8> = Vec::new();

    for (key, values) in &argsKeys {
        let valuesLength: usize = (&values).len();
        // run file
        if key == "-rf" {
            unsafe{
                _argc = valuesLength-1;
                _argv = values.clone();
                _argv.remove(0); // remove file name
                _filePath = values[0].clone();
            }
            // todo: check filePath file type
            noRun = false;
            if unsafe{_debugMode} {
                log("ok",&format!("Run \"{}\"",unsafe{&*_filePath}));
            }
            runFile = true;
        } else 
        // run script
        if key == "-rs" {
            //unsafe{
                let combinedString = values.concat().replace("\\n", "\n"); // todo: \\n ?
                buffer = combinedString.clone().into_bytes();
                // todo:
                //_argc = valuesLength-1;
                //_argv = values.clone();
                //_argv.remove(0); // remove file name
                //_filePath = values[0].clone();
            //}
            // todo: check filePath file type
            noRun = false;
            if unsafe{_debugMode} {
                log("ok",&format!("Run \"{}\"",combinedString));
            }
        }
    }
    
    if noRun {
        log("err","Use the [-rf <filename>] or [-rs \"<script>\"] flag");
        logExit();
    }

    // run file
    if runFile {
        if unsafe{_debugMode} {
            logSeparator("=> Opening a file");
        }
        // open file
        let mut file = match File::open(unsafe{&*_filePath}) {
            Ok(file) => {
                if unsafe{_debugMode} {
                    log("ok",&format!("Opening the file \"{}\" was successful",unsafe{&*_filePath}));
                }
                file
            },
            Err(_) => {
                log("err",&format!("Unable to opening file \"{}\"",unsafe{&*_filePath}));
                logExit();
                std::process::exit(1)
            }
        };
        // read file into buffer
        match file.read_to_end(&mut buffer) {
            Ok(_) => {
                // add endl if it doesn't exist
                if !buffer.ends_with(&[b'\n']) {
                    buffer.push(b'\n');
                }
                if unsafe{_debugMode} {
                    log("ok",&format!("Reading the file \"{}\" was successful",unsafe{&*_filePath}));
                }
            }
            Err(_) => {
                log("err",&format!("Unable to read file \"{}\"",unsafe{&*_filePath}));
                logExit();
                ()
            }
        }
    // run script
    } else
    if unsafe{_debugMode} {
        logSeparator("=> Read script");
    }

    if unsafe{_debugMode} {
        logSeparator("=> AST generation");
    }

    // read
    unsafe {
        parseLines( readTokens(buffer) );
    }

    //
    if unsafe{_debugMode} {
        let endTime  = Instant::now();
        let duration = endTime-startTime;
        logSeparator( &format!("=> Duration: {:?}",duration) );
    }
    // to release test, use hyperfine

    //
    Ok(())
}
