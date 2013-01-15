OFFSET_PERCENT = 30
def mess(x)
   x + (x * rand(OFFSET_PERCENT) / 100.0) * [-1, 1].sample
end

def c(a,b,r); tt = t; [(a + r * Math.cos(tt)).round.to_i, (b + r * Math.sin(tt)).round.to_i]; end
def t; rand(100) / 100.0 * 2 * Math::PI; end

raw = 30.times.map do 
  x, y = c(0, 0, 300)
  [mess(x).round.to_i, mess(y).round.to_i, "true"].join(",") 
end

# raw += 150.times.map do
#   [rand(800).round.to_i * [-1, 1].sample, rand(800).round.to_i * [-1, 1].sample , "false"].join(",") 
# end

File.open("pc.data", "w") { |file| file.write(raw.shuffle.join("\n")) }
