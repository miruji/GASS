fn ab(a: i32, b: i32) -> i32 {
    a + b
}

fn main() {
    let mut i = 0;
    while i < 10 {
        if i == 0 {
            println!("i == 0!");
        }
        println!("Hello {}!", i);
        i += 1;
    }
}
