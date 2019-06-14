puts "This is a training Ruby Program"

END {  # happens after everything else in program
   puts "Terminating Ruby Program"
}

BEGIN {  # happens before everything else in the program
   puts "Initializing Ruby Program"
}

class Customer
   @@no_of_customers = 0

   def initialize(id, name, addr)  # constructor
      @cust_id = id
      @cust_name = name
      @cust_addr = addr
      @@no_of_customers += 1
   end

   def display_details()
      puts "Customer id #@cust_id"
      puts "Customer name #@cust_name"
      puts "Customer address #@cust_addr"
   end

   def display_no_of_customers()
      puts "Total number of customers: #@@no_of_customers"
   end
end
# Create Objects
cust1 = Customer.new("1", "John", "Wisdom Apartments, Ludhiya")
cust2 = Customer.new("2", "Poul", "New Empire road, Khandala")
# Call Methods
cust1.display_no_of_customers()
cust2.display_no_of_customers()

puts "Multiplication Value : #{24*60*60}";  # substituting the value of any Ruby expression with #{ expr }

array = [ "fred", 10, 3.14, "This is a string", "last element" ]
array.each do |i|
   puts i
end

hsh = colors = { "red" => 0xf00, "green" => 0x0f0, "blue" => 0x00f }
hsh.each do |key, value|
   print key, " is ", value, "\n"
end

(10..15).each do |n| 
   print n, ' '  # 10 11 12 13 14 15
end
(1...5).each do |i|
   print i, ' '  # 1 2 3 4
end

puts 5**3  # 125
puts 5**2  # 25
puts 5 <=> 5  # 0
puts 6 <=> 5  # 1
puts 4 <=> 5  # -1
puts (1...10) === 5  # true
puts 1 == 1.0  # true
puts 1.eql?(1.0)  # false (needs to be equal value and type)
# if aObj is duplicate of bObj then aObj == bObj is true, a.equal?bObj is false but a.equal?aObj is true
=begin
 a    =  0011 1100
 b    =  0000 1101
 ------------------
 a&b  =  0000 1100
 a|b  =  0011 1101
 a^b  =  0011 0001
 ~a   =  1100 0011
=end
# defined? <expr> - prints if a variable or method has been defined
puts defined? hsh  # true

x = 1
# if condition [then]
#  statement[s]
# end
# The 'then' statement is not neccessary
if x > 2  # executes code if condition is true
   puts "x is greater than 2"
elsif x <= 2 and x != 0
   puts "x is 1"
else
   puts "I can't guess the number"
end
$debug = 1
print "debug\n" if $debug  # conditional statement (like first half of ternary operator)
x = 1
unless x >= 2  # executes code if condition is false
   puts "x is less than 2"
 else
   puts "x is greater than 2"
end
$var =  1
print "1 -- Value is set\n" if $var  # printed
print "2 -- Value is set\n" unless $var  # not printed
$var = false
print "3 -- Value is set\n" unless $var  # printed

$age = 5
case $age
when 0 .. 2
   puts "baby"
when 3 .. 6
   puts "little child"
when 7 .. 12
   puts "child"
when 13 .. 18
   puts "youth"
else
   puts "adult"
end

$i = 0
$num = 5
while $i < $num  do
   puts("Inside the loop i = #$i" )
   $i += 1
end

$i = 0
$num = 5
until $i > $num  do
   puts("Inside the loop i = #$i" )
   $i += 1;
end

for i in 0..5
   puts "Value of local variable is #{i}"
end
=begin
Flow control statements:
- break -> terminates the most internal loop
- next -> jumps to the next iteraiton of the most internal loop
- redo -> restarts this iteration of the most inner loop (goes in an infinite loop)
=end

# method with default parameter values
def _test(a1 = "Ruby", a2 = "Perl")
   puts "The programming language is #{a1}"
   puts "The programming language is #{a2}"
end
_test "C", "C++"
_test
# method with varying amount of arguments
def sample (*test)
   puts "The number of parameters is #{test.length}"
   for i in 0...test.length
      puts "The parameters are #{test[i]}"
   end
end
sample "Zara", "6", "F"
sample "Mac", "36", "M", "MCA"

$bar = 0
alias $foo $bar  # bar can be accessed with the 'foo' alias

def _method
   puts "message"
end
undef _method  # undefines the method '_method'
