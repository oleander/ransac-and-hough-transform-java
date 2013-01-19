OFFSET_PERCENT = 0
def mess(x)
   x + (x * rand(OFFSET_PERCENT) / 100.0) * [-1, 1].sample
end

def c(a,b,r); tt = t; [(a + r * Math.cos(tt)).round.to_i, (b + r * Math.sin(tt)).round.to_i]; end
def t; rand(100) / 100.0 * 2 * Math::PI; end

raw = 100.times.map do 
  x, y = c(0, 0, 50)
  [mess(x).round.to_i, mess(y).round.to_i].join(",") 
end

raw += 100.times.map do 
  x, y = c(100, 140, 110)
  [mess(x).round.to_i, mess(y).round.to_i].join(",") 
end

raw += 100.times.map do 
  x, y = c(0, 50, 50)
  [mess(x).round.to_i, mess(y).round.to_i].join(",") 
end

raw += 100.times.map do 
  x, y = c(20, 100, 100)
  [mess(x).round.to_i, mess(y).round.to_i].join(",") 
end

# raw += 100.times.map do 
#   [rand(200) * [-1, 1].sample, rand(200) * [-1, 1].sample, "true"].join(",") 
# end

# raw += 150.times.map do
#   [rand(800).round.to_i * [-1, 1].sample, rand(800).round.to_i * [-1, 1].sample , "false"].join(",") 
# end

File.open("pc.data", "w") { |file| file.write(raw.shuffle.join("\n")) }
